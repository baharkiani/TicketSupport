package com.example.TicketSupport.policy;

import com.example.TicketSupport.entity.Department;
import com.example.TicketSupport.entity.Ticket;
import com.example.TicketSupport.entity.User;
import com.example.TicketSupport.repository.TicketRepository;
import org.springframework.stereotype.Component;


@Component
public class DepartmentPolicy implements AuthorizationPolicy {

    private final RequestDepartmentResolver requestDepartmentResolver;
    private final TicketRepository ticketRepository;

    public DepartmentPolicy(RequestDepartmentResolver requestDepartmentResolver
    , TicketRepository ticketRepository) {
        this.requestDepartmentResolver = requestDepartmentResolver;
        this.ticketRepository = ticketRepository;
    }

//    @Override
//    public boolean supports(PolicyContext context) {
//
//        return context.user() != null && context.user().getDepartment() != null;
//    }
@Override
public boolean supports(PolicyContext context) {

    if (context.user() == null ||
            context.permission() == null) {
        return false;
    }

    // CREATE نیاز به بررسی Ticket موجود ندارد
    if ("CREATE".equals(context.permission().action())) {
        return true;
    }

    // سایر عملیات باید روی Ticket موجود بررسی شوند
    return true;
}

//    @Override
//    public boolean evaluate(PolicyContext context) {
//
//        User user = context.user();
//
//        String action = context.permission().action();
//
//        // =========================
//        // CREATE
//        // =========================
//        if ("CREATE".equals(action)) {
//
//            // دپارتمان Ticket در Service
//            // از دپارتمان User گرفته می‌شود.
//            return user.getDepartment() != null;
//        }
//
//        // =========================
//        // READ / UPDATE / DELETE
//        // =========================
//
//        String id = extractTicketId(context);
//
//        if (id == null) {
//            return false;
//        }
//
//        Ticket ticket = ticketRepository
//                .findById(Long.parseLong(id))
//                .orElse(null);
//
//        if (ticket == null) {
//            return false;
//        }
//
//        Department userDepartment =
//                user.getDepartment();
//
//        Department ticketDepartment =
//                ticket.getDepartment();
//
//        if (userDepartment == null ||
//                ticketDepartment == null) {
//            return false;
//        }
//
//        return userDepartment.getId()
//                .equals(ticketDepartment.getId());
//    }

    @Override
    public boolean evaluate(PolicyContext context) {

        User user = context.user();
        String action = context.permission().action();

        // CREATE
        if ("CREATE".equals(action)) {
            return user.getDepartment() != null;
        }

        // READ / UPDATE / DELETE
        String id = extractTicketId(context);

        // GET /api/tickets
        // اینجا id وجود ندارد.
        // محدودسازی دپارتمان باید در Service/Repository انجام شود.
        if (id == null) {
            return true;
        }

        Ticket ticket = ticketRepository
                .findById(Long.parseLong(id))
                .orElse(null);

        if (ticket == null) {
            return false;
        }

        Department userDepartment = user.getDepartment();
        Department ticketDepartment = ticket.getDepartment();

        if (userDepartment == null || ticketDepartment == null) {
            return false;
        }

        return userDepartment.getId()
                .equals(ticketDepartment.getId());
    }

    private String extractTicketId(
            PolicyContext context
    ) {

        Object value = context.request()
                .getAttribute(
                        org.springframework.web.servlet.HandlerMapping
                                .URI_TEMPLATE_VARIABLES_ATTRIBUTE
                );

        if (!(value instanceof java.util.Map<?, ?> variables)) {
            return null;
        }

        Object id = variables.get("id");

        return id != null
                ? id.toString()
                : null;
    }

}

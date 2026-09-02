package com.example.TicketSupport.policy;

import com.example.TicketSupport.entity.Department;
import com.example.TicketSupport.entity.Ticket;
import com.example.TicketSupport.entity.User;
import com.example.TicketSupport.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DepartmentPolicy implements AuthorizationPolicy {

    private final TicketRepository ticketRepository;




@Override
public boolean supports(PolicyContext context) {

    if (context.user() == null ||
            context.permission() == null) {
        return false;
    }
    if ("CREATE".equals(context.permission().action())
            || "GET".equals(context.permission().action())) {
        return false;
    }

    return true;
}


    @Override
    public boolean evaluate(PolicyContext context) {

        User user = context.user();
        if (user.getDepartment() == null) {
            return false;
        }


        // READ / UPDATE / DELETE
        String id = extractTicketId(context);

        if (id == null) {
            return true;
        }

        Ticket ticket = ticketRepository.findById(Long.parseLong(id)).orElse(null);
        if (ticket == null) {
            return false;
        }

        Department userDepartment = user.getDepartment();
        Department ticketDepartment = ticket.getDepartment();

        if (userDepartment == null || ticketDepartment == null) {
            return false;
        }

        return userDepartment.getId().equals(ticketDepartment.getId());
    }



    private String extractTicketId(
            PolicyContext context
    ) {

        Object value = context.request().getAttribute(
                org.springframework.web.servlet.HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE
                );

        if (!(value instanceof java.util.Map<?, ?> variables)) {
            return null;
        }

        Object id = variables.get("id");

        return id != null ? id.toString() : null;
    }

}

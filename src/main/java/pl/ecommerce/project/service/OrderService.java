package pl.ecommerce.project.service;

import org.springframework.stereotype.Service;
import pl.ecommerce.project.payload.dto.OrderDTO;

@Service
public class OrderService {
    public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName,
                               String pgPaymentId, String pgStatus, String pgResponseMessage) {

    }
}

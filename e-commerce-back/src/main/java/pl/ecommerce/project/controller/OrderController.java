package pl.ecommerce.project.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.ecommerce.project.payload.dto.OrderDTO;
import pl.ecommerce.project.payload.dto.OrderRequestDTO;
import pl.ecommerce.project.payload.dto.StripePaymentDTO;
import pl.ecommerce.project.service.OrderService;
import pl.ecommerce.project.service.StripeService;
import pl.ecommerce.project.util.AuthUtil;

@RestController
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;
    private final AuthUtil authUtil;
    private final StripeService stripeServices;

    public OrderController(OrderService orderService, AuthUtil authUtil, StripeService stripeServices) {
        this.orderService = orderService;
        this.authUtil = authUtil;
        this.stripeServices = stripeServices;
    }

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProduct(@PathVariable String paymentMethod,
                                                 @RequestBody OrderRequestDTO orderRequestDTO) {
        String emailId = authUtil.loggedInEmail();
        System.out.println("orderRequestDTO DATA: " + orderRequestDTO);
        OrderDTO order = orderService.placeOrder(
                emailId,
                orderRequestDTO.getAddressId(),
                paymentMethod,
                orderRequestDTO.getPgName(),
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage()
        );
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @PostMapping("/order/stripe-client-secret")
    public ResponseEntity<String> createStripeClientSecret(@RequestBody StripePaymentDTO stripePaymentDTO ) throws StripeException {
        PaymentIntent paymentIntent = stripeServices.paymentIntent(stripePaymentDTO);
        return new ResponseEntity<>(paymentIntent.getClientSecret(), HttpStatus.CREATED);
    }
}

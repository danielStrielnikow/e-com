package pl.ecommerce.project.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.ecommerce.project.model.User;
import pl.ecommerce.project.payload.dto.AddressDTO;
import pl.ecommerce.project.service.AddressService;
import pl.ecommerce.project.util.AuthUtil;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class AddressController {
    private final AddressService addressService;
    private final AuthUtil authUtil;

    public AddressController(AddressService addressService, AuthUtil authUtil) {
        this.addressService = addressService;
        this.authUtil = authUtil;
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        User user = authUtil.loggedInUser();
        AddressDTO savedAddressDTO = addressService.createAddress(addressDTO, user);
        return new ResponseEntity<>(savedAddressDTO, HttpStatus.CREATED);

    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddress() {
        List<AddressDTO> address = addressService.getAllAddresses();
        return new ResponseEntity<>(address, HttpStatus.OK);
    }

    @GetMapping("/user/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddress() {
        User user = authUtil.loggedInUser();
        List<AddressDTO> userAddress = addressService.getUserAddresses(user);
        return new ResponseEntity<>(userAddress, HttpStatus.OK);
    }

    @GetMapping("/addresses/{addressesId}")
    public ResponseEntity<AddressDTO> getAddress(@PathVariable Long addressesId) {
        AddressDTO address = addressService.getAddressById(addressesId);
        return new ResponseEntity<>(address, HttpStatus.OK);
    }

    @PutMapping("/addresses/{addressesId}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long addressesId,
                                                    @RequestBody AddressDTO addressDTO) {
        AddressDTO address = addressService.updateAddress(addressesId, addressDTO);
        return new ResponseEntity<>(address, HttpStatus.OK);
    }


    @DeleteMapping("/addresses/{addressesId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressesId) {
        String status = addressService.deleteAddress(addressesId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}

package pl.ecommerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.ecommerce.project.exception.ResourceNotFoundException;
import pl.ecommerce.project.model.Address;
import pl.ecommerce.project.model.User;
import pl.ecommerce.project.payload.dto.AddressDTO;
import pl.ecommerce.project.payload.dto.DTOMapper;
import pl.ecommerce.project.repo.AddressRepository;
import pl.ecommerce.project.repo.UserRepository;

import java.util.List;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final DTOMapper dtoMapper;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository, DTOMapper dtoMapper) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.dtoMapper = dtoMapper;
    }

    @Transactional
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = dtoMapper.mapAddressToEntity(addressDTO);
        List<Address> addressList = user.getAddresses();
        addressList.add(address);

        user.setAddresses(addressList);
        address.setUser(user);
        Address savedAddress = addressRepository.save(address);

        return dtoMapper.mapToAddressDTO(savedAddress);
    }

    private User fetchUserById(Long user) {
        return userRepository.findById(user)
                .orElseThrow(() -> new ResourceNotFoundException("User", "user", user));
    }
}

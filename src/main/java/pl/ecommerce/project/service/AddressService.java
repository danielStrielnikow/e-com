package pl.ecommerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.ecommerce.project.exception.APIException;
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

    public List<AddressDTO> getAllAddresses() {
        List<Address> addressList = addressRepository.findAll();

        if (addressList.isEmpty()) {
            throw new APIException("No address exists");
        }

        return addressList.stream()
                .map(dtoMapper::mapToAddressDTO)
                .toList();
    }

    public AddressDTO getAddressById(Long addressId) {
        Address address = fetchAddressById(addressId);
        return dtoMapper.mapToAddressDTO(address);
    }


    public List<AddressDTO> getUserAddresses(User user) {
        List<Address> addressList = user.getAddresses();

        if (addressList.isEmpty()) {
            throw new APIException("No address exists");
        }

        return addressList.stream()
                .map(dtoMapper::mapToAddressDTO)
                .toList();
    }

    @Transactional
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        Address address = fetchAddressById(addressId);

        updateAddressFromDTO(addressDTO, address);
        Address updatedAddress = addressRepository.save(address);

        User user = address.getUser();
        user.getAddresses().removeIf(a -> a.getAddressId().equals(addressId));
        user.getAddresses().add(updatedAddress);
        userRepository.save(user);
        return dtoMapper.mapToAddressDTO(updatedAddress);
    }

    @Transactional
    public String deleteAddress(Long addressId) {
        Address address = fetchAddressById(addressId);

        User user = address.getUser();
        user.getAddresses().removeIf(a -> a.getAddressId().equals(addressId));
        userRepository.save(user);

        addressRepository.delete(address);
        return "address deleted successfully with addressId" + addressId;
    }


    private Address fetchAddressById(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "address", addressId));
    }

    private void updateAddressFromDTO(AddressDTO addressDTO, Address address) {
        address.setStreet(addressDTO.getStreet());
        address.setBuildingName(addressDTO.getBuildingName());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setCountry(addressDTO.getCountry());
        address.setPinCode(addressDTO.getPinCode());
    }
}

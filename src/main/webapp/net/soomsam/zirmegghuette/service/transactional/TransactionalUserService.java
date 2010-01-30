package net.soomsam.zirmegghuette.service.transactional;

import net.soomsam.zirmegghuette.service.UserService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userService")
@Transactional(timeout = 1000)
public class TransactionalUserService implements UserService {
}

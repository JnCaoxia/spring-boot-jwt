package murraco.service;

import murraco.manager.CacheManager;
import murraco.model.AppUser;
import murraco.model.AppUserRole;
import murraco.model.dto.AppUserDTO;
import murraco.repository.UserRepository;
import murraco.utils.JavaBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserCacheService extends AbstractCacheService<Integer, AppUserDTO>{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public UserCacheService(RedisTemplate<String, Object> redisTemplate, CacheManager cacheManager) {
        super(redisTemplate, cacheManager);
    }

    @Override
    protected AppUserDTO loadFromDatabase(Integer id) {
        Optional<AppUser> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            AppUser appUser = userOptional.get();
            return JavaBean.copyOfThenSet(appUser, AppUserDTO::new, v -> v.setAppUserRoles(appUser.getAppUserRoles().stream()
                    .map(AppUserRole::toString)
                    .collect(Collectors.toList())));
        }
        return null;
    }

    @Override
    protected String getRedisKey(Integer id) {
        return "user:" + id;
    }
}

package murraco.service;

import com.github.benmanes.caffeine.cache.Cache;
import murraco.manager.CacheManager;
import murraco.model.AppUser;
import murraco.model.AppUserRole;
import murraco.model.dto.AppUserDTO;
import murraco.repository.UserRepository;
import murraco.utils.JavaBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserDataService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private final Cache<Integer, AppUserDTO> userCache;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    public UserDataService(CacheManager cacheManager) {
        this.userCache = cacheManager.createCache(1000, 10);
    }

    @Autowired
    private UserRepository userRepository;

    public AppUserDTO getUserById(Integer id) {
        // 1. 从本地缓存中获取
        AppUserDTO user = cacheManager.getFromCache(userCache, id);
        if (user != null) {
            return user;
        }

        // 2. 从 Redis 缓存中获取
        user = (AppUserDTO) redisTemplate.opsForValue().get("user:" + id);
        if (user != null) {
            cacheManager.putInCache(userCache, id, user);  // 加入本地缓存
            return user;
        }

        // 3. 从数据库中获取
        Optional<AppUser> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            AppUser appUser = userOptional.get();
            user = JavaBean.copyOfThenSet(appUser, AppUserDTO::new, v-> v.setAppUserRoles(appUser.getAppUserRoles().stream()
                    .map(AppUserRole::toString)
                    .collect(Collectors.toList())));
            cacheManager.putInCache(userCache, id, user);  // 加入本地缓存
            redisTemplate.opsForValue().set("user:" + id, user);  // 加入 Redis 缓存
        }

        return user;
    }

    @Transactional
    public AppUser createUser(String name, String email) {
        AppUser user = new AppUser();
        user.setUsername(name);
        user.setEmail(email);
        return userRepository.save(user);
    }
}

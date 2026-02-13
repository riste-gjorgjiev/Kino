package mk.ukim.finki.wp.kino.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheConfig {
    @Bean
    public CacheManager cacheManager(){
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager(
                "trendingMovies", "popularMovies", "topRatedMovies", "upcomingMovies",
                "trendingTv", "popularTv", "topRatedTv", "airingTvShows",
                "searchMovies", "searchTv"

        );

        caffeineCacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(10))
                        .maximumSize(1000)
        );
        return caffeineCacheManager;
    }
}

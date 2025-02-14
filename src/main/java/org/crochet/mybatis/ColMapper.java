package org.crochet.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.crochet.model.Collection;

@Mapper
public interface ColMapper {
    @Select("""
            select c.id, c.name, c.avatar from collection c
            left join collection_free_pattern cfp on cfp.collection_id = c.id
            left join free_pattern fp on cfp.free_pattern_id = fp.id
            join users u on u.id = c.user_id
            where u.id = #{userId}
            and fp.id = #{frepId}
            """)
    Collection findAllByUser(String userId, String frepId);
}

package com.mmall.dao;

import com.mmall.pojo.Order;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByOrderNo(Long orderNo);

    Order selectByOrderNoAndUserId(@Param("orderNo") Long orderNo, @Param("userId") Integer userId);

    List<Order> selectOrderListByUserId(Integer userId);

    List<Order> selectAllOrder();

//    这里比时间大小，数据库的TIMESTAMP可以直接和String的time去做比较
    List<Order> selectOrderByStatusAndCreateDate(@Param("status") int status, @Param("createDate") String createDate);

    int updateStatusBatch(@Param("orderList") List<Order> orderList);
}
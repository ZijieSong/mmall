package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.OrderService;
import com.mmall.vo.OrderVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/manage/order/")
public class OrderManageController {

    @Resource(name = "orderService")
    private OrderService orderService;


    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> manageOrderList(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                                    @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        return orderService.manageOrderList(pageNum,pageSize);
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVO> manageDetail(Long orderNo){
        return orderService.manageOrderDetail(orderNo);
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> search(Long orderNo,
                                 @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                 @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){

        return orderService.manageSearch(orderNo, pageNum, pageSize);
    }

    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse sendGoods(Long orderNo){
        return orderService.manageSendGoods(orderNo);
    }

}

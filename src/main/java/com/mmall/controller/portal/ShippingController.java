package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.HostHolder;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/shipping/")
public class ShippingController {

    @Autowired
    HostHolder hostHolder;
    @Resource(name = "shippingService")
    private ShippingService shippingService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse addShipping(Shipping shipping) {
        User user = hostHolder.getUser();
        return shippingService.addShipping(user.getId(), shipping);
    }

    @RequestMapping("del.do")
    @ResponseBody
    public ServerResponse deleteShipping(Integer shippingId) {
        User user = hostHolder.getUser();
        return shippingService.deleteShipping(user.getId(), shippingId);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse updateShipping(Shipping shipping) {
        User user = hostHolder.getUser();
        return shippingService.updateShipping(user.getId(), shipping);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<Shipping> selectShipping(Integer shippingId) {
        User user = hostHolder.getUser();
        return shippingService.selectShipping(user.getId(), shippingId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> selectShippingList(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                                       @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize) {
        User user = hostHolder.getUser();
        return shippingService.getShippingList(user.getId(),pageNum,pageSize);
    }

}

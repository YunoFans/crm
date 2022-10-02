package com.xxz.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.xxz.bean.Customer;
import com.xxz.bean.CustomerExample;
import com.xxz.bean.Employee;
import com.xxz.listener.DemoDataListener;
import com.xxz.mapper.CustomerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerMapper customerMapper;

    //条件查询
    @RequestMapping("/customers")
    public String queryAll(Model model, String cRename, String cName, String cJob) throws UnsupportedEncodingException {
//        System.out.println(URLEncoder.encode(eJob,"utf-8"));
        System.out.println("queryAll emp by confition:" + cRename + "-" + cName + "-" + cJob);
        //样本
        CustomerExample customerExample = new CustomerExample();
        //条件盒子
        CustomerExample.Criteria criteria = customerExample.createCriteria();
        //追加条件
        if (cRename != null){
            criteria.andCNameLike("%" + cRename + "%");
        }
        if(cJob != null && !cJob.equals("")){
            criteria.andCJobEqualTo(cJob);
        }
        if(cName != null && !cName.equals("")){
            criteria.andCNameEqualTo(cName);
        }
        //查询
        List<Customer> customerList = customerMapper.selectByExample(customerExample);
        System.out.println("条件查询结果：" + customerList);
        model.addAttribute("customerList", customerList);
        return "customer/customer";
    }

    //id查询
    @RequestMapping("/queryById/{cId}")
    @ResponseBody
    public Map<String,Object> queryById(@PathVariable("cId") Integer cId, HttpServletResponse response) throws IOException {
        Customer customer = customerMapper.selectByPrimaryKey(cId);
        Map<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("code","200");
        stringObjectHashMap.put("data",customer);
//        response.getWriter().println(Customer);
        return stringObjectHashMap;
    }

    //新增员工
    @RequestMapping("/add")
    public String cusAdd(Customer customer){
        System.out.println("add cus..." + customer);
        //调用接口将数据添加到数据库
        customerMapper.insertSelective(customer);
        return "redirect:/customer/customers";
    }

    //删除员工
    @RequestMapping("/del/{eId}")
    public String cusDel(@PathVariable("eId") Integer eId){
        System.out.println(eId);
        //删除业务
        customerMapper.deleteByPrimaryKey(eId);
        //重定向到empList界面展示最新数据
        return "redirect:/customer/customers";
    }

    //修改
    @RequestMapping("/update")
    public String cusUpdate(Customer customer){
        //展示emp
        System.out.println(customer);
        //调用目标接口实现信息修改
        customerMapper.updateByPrimaryKeySelective(customer);
        //重定向到empMenu界面
        return "redirect:/customer/customers";
    }

    /*excel导入导出*/
    @RequestMapping(value = "/excelInport", method = RequestMethod.POST)
    public String excelInport(MultipartFile importFile){
        System.out.println("文件名：" + importFile.getOriginalFilename());
//        System.out.println("input.........................");
//        String fileName = "E:\\员工信息表.xlsx";
//        //EasyExcel.read(fileName, DemoData.class, new DemoDataListener()).sheet().doRead();
//        EasyExcel.read(fileName, Employee.class, new DemoDataListener()).sheet().doRead();
        // 解析Excel
        ExcelReader excelReader = null;
        try {
            excelReader = EasyExcelFactory.read(importFile.getInputStream(), Customer.class, new DemoDataListener<Customer>()).build();
            ReadSheet readSheet = EasyExcelFactory.readSheet(0).build();
            excelReader.read(readSheet);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (excelReader != null) {
                excelReader.finish();
            }
        }
        //重定向到empMenu界面
        return "redirect:/customer/customers";
    }

    //导出
    @RequestMapping("/excelOutput") //ResponseEntity<byte[]>
    public void excelOutput(HttpSession session, HttpServletResponse response) throws IOException {
        //获取本机桌面地址
//        File desktopDir = FileSystemView.getFileSystemView() .getHomeDirectory();
//        String PATH = desktopDir.getAbsolutePath() + "\\";
//        String fileName = PATH + "员工信息表.xlsx";
        //设置响应头和响应体格式，告诉浏览器是什么文件，对应解析
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename="
                + URLEncoder.encode("客户信息", "UTF-8") + ".xlsx");
        //获取目标数据
        List<Customer> customers = customerMapper.selectByExample(null);
        //响应到浏览器
        EasyExcel.write(response.getOutputStream(), Customer.class).sheet("模板").doWrite(customers);
        System.out.println("excelOutput.................");
    }

}
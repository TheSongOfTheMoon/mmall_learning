package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service("iCategoryService")//生成以方便被Controller注入
@Slf4j
public class ICategoryServiceImpl implements ICategoryService {

    //private Logger logger= LoggerFactory.getLogger(ICategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse addCategory(String categoryName,Integer parentId){

        //参数校验
        if (parentId==null || org.apache.commons.lang3.StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        //新增一个节点
        Category category=new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);

        int insertCount= categoryMapper.insert(category);
        if (insertCount>0){
            return ServerResponse.createBySuccessMessage("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    public ServerResponse  updateCatrgoryName(String categoryName,Integer categoryId){
        //参数校验
        if (categoryId==null || org.apache.commons.lang3.StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        //更新一个节点
        Category category=new Category();
        category.setParentId(categoryId);
        category.setName(categoryName);
        int updateCount=categoryMapper.updateByPrimaryKeySelective(category);
        if (updateCount>0){
            return ServerResponse.createBySuccess("更新品类成功",category);
        }else{
            return ServerResponse.createByErrorMessage("更新品类失败");
        }
    }


    public  ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category> categoryList=categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)){
            log.info("未找到当前分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }




    public  ServerResponse<List<Integer>> selectCategoryAndChilerenById(Integer categoryId){
        Set<Category> categorySet=new HashSet<>();
        findChildCategory(categorySet,categoryId);

        List<Integer>  categorylist= Lists.newArrayList();//创建一个容器
        if (categoryId !=null){
            for (Category categoryItem:categorySet){
                categorylist.add(categoryId);
            }
        }
        return ServerResponse.createBySuccess(categorylist);
    }

    //递归节点：利用HashSet是因为Set无序不重复
    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){
        Category category=categoryMapper.selectByPrimaryKey(categoryId);/*类型Id*/
        if (category!=null){
            categorySet.add(category);
        }
        List<Category>  categorylist=categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for(Category categoryItem :categorylist){
            findChildCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }

    /*
    * 类型表每条记录有自己的id也有自己的一个节点id,其中id是自动递增，但是子节点id是可认为维护的
    * 每次都是以查出来的id为条件查找，如果查到不为空，则添加进Set列表中，进列表，查到为空则跳过
    *
    *每次提供一个categoryId节点,先出该节点的记录，有多少个，存储多少个进列表
    *然后根据该对象的categoryId作为递归节点去查询，查出多少个则获取多少个，直到以递归节点去查询再也找不到下级节点
    *原则:类型节点的子节点必须为0，代表着最大级别
    * */

}

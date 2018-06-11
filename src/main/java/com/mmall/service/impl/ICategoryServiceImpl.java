package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service("iCategoryService")//生成以方便被Controller注入
public class ICategoryServiceImpl implements ICategoryService {

    private Logger logger= LoggerFactory.getLogger(ICategoryServiceImpl.class);

    @Autowired
    private CartMapper cartMapper;

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

        int insertCount= cartMapper.insert(category);
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
        int updateCount=cartMapper.updateByPrimaryKeySelective(category);
        if (updateCount>0){
            return ServerResponse.createBySuccess("更新品类成功",category);
        }else{
            return ServerResponse.createByErrorMessage("更新品类失败");
        }
    }


    public  ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category> categoryList=cartMapper.selectCategoryChildrenByCategoryId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)){
            logger.info("未找到当前分类");
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

    //递归节点
    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){
        Category category=cartMapper.selectByPrimaryKey(categoryId);
        if (category!=null){
            categorySet.add(category);
        }
        List<Category>  categorylist=cartMapper.selectCategoryChildrenByCategoryId(categoryId);
        for(Category categoryItem :categorylist){
            findChildCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }



}

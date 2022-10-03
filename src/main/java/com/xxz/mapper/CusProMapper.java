package com.xxz.mapper;

import com.xxz.bean.CusPro;
import com.xxz.bean.CusProExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CusProMapper {
    int countByExample(CusProExample example);

    int deleteByExample(CusProExample example);

    int deleteByPrimaryKey(Integer cpId);

    int insert(CusPro record);

    int insertSelective(CusPro record);

    List<CusPro> selectByExample(CusProExample example);

    CusPro selectByPrimaryKey(Integer cpId);

    int updateByExampleSelective(@Param("record") CusPro record, @Param("example") CusProExample example);

    int updateByExample(@Param("record") CusPro record, @Param("example") CusProExample example);

    int updateByPrimaryKeySelective(CusPro record);

    int updateByPrimaryKey(CusPro record);
}
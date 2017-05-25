package com.ipeaksoft.moneyday.core.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ipeaksoft.moneyday.core.entity.ClusterDms;

public interface ClusterDmsMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(ClusterDms record);

	int insertSelective(ClusterDms record);

	ClusterDms selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(ClusterDms record);

	int updateByPrimaryKey(ClusterDms record);

	ClusterDms checkDms(@Param("mac") String mac);
	
	List<ClusterDms> selectAll();
}
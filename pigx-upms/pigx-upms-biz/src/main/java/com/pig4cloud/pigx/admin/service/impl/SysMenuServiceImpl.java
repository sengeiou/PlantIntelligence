/*
 *
 *      Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the pig4cloud.com developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: lengleng (wangiegie@gmail.com)
 *
 */

package com.pig4cloud.pigx.admin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.pig4cloud.pigx.admin.api.entity.SysMenu;
import com.pig4cloud.pigx.admin.api.entity.SysRoleMenu;
import com.pig4cloud.pigx.admin.api.vo.MenuVO;
import com.pig4cloud.pigx.admin.mapper.SysMenuMapper;
import com.pig4cloud.pigx.admin.mapper.SysRoleMenuMapper;
import com.pig4cloud.pigx.admin.service.SysMenuService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单权限表 服务实现类
 * </p>
 *
 * @author lengleng
 * @since 2017-10-29
 */
@Service
@AllArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
	private final SysRoleMenuMapper sysRoleMenuMapper;

	@Override
	@Cacheable(value = "menu_details", key = "#roleId  + '_menu'")
	public List<MenuVO> findMenuByRoleId(Integer roleId) {
		return baseMapper.findMenuByRoleId(roleId);
	}

	@Override
	@CacheEvict(value = "menu_details", allEntries = true)
	public Boolean deleteMenu(Integer id) {
		// 查询父节点为当前节点的节点
		SysMenu conditon = new SysMenu();
		conditon.setParentId(id);
		List<Integer> menuIdList = this.selectList(new EntityWrapper<>(conditon))
			.stream().map(SysMenu::getMenuId)
			.collect(Collectors.toList());
		menuIdList.add(id);

		//删除关联ROLE_MENU 数据
		menuIdList.forEach(menu -> {
			SysRoleMenu conditon2 = new SysRoleMenu();
			conditon2.setMenuId(menu);
			sysRoleMenuMapper.delete(new EntityWrapper<>(conditon2));
		});
		//删除当前菜单及其子菜单
		return this.deleteBatchIds(menuIdList);
	}

	@Override
	@CacheEvict(value = "menu_details", allEntries = true)
	public Boolean updateMenuById(SysMenu sysMenu) {
		return this.updateById(sysMenu);
	}
}

package com.onlineEdu.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.onlineEdu.base.exception.OnlineEduException;
import com.onlineEdu.content.mapper.TeachplanMapper;
import com.onlineEdu.content.mapper.TeachplanMediaMapper;
import com.onlineEdu.content.model.dto.BindTeachplanMediaDto;
import com.onlineEdu.content.model.dto.SaveTeachplanDto;
import com.onlineEdu.content.model.dto.TeachplanDto;
import com.onlineEdu.content.model.po.Teachplan;
import com.onlineEdu.content.model.po.TeachplanMedia;
import com.onlineEdu.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Astronaut
 * @version 1.0
 * @description
 * @date 2022/10/10 14:51
 */
@Slf4j
@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    //新增、修改
    @Override
    public void saveTeachplan(SaveTeachplanDto dto) {
        Long id = dto.getId();

        Teachplan teachplan = teachplanMapper.selectById(id);

        if (teachplan == null) {
            teachplan = new Teachplan();
            BeanUtils.copyProperties(dto, teachplan);
            //找到同级课程计划的数量
            int count = getTeachplanCount(dto.getCourseId(), dto.getParentid());
            //新课程计划的值
            teachplan.setOrderby(count + 1);
            teachplanMapper.insert(teachplan);
        } else {
            BeanUtils.copyProperties(dto, teachplan);
            //更新
            teachplanMapper.updateById(teachplan);
        }
    }

    @Override
    public void deleteTeachplan(Long teachPlanId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getId, teachPlanId).or().eq(Teachplan::getParentid, teachPlanId);

        teachplanMapper.delete(queryWrapper);
    }

    @Override
    public void moveUp(Long teachplanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        int orderby = teachplan.getOrderby();
        int teachplanCount = getTeachplanCount(teachplanId, teachplan.getParentid());
        if (teachplanCount > 1) {
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getOrderby, teachplanCount - 1).eq(Teachplan::getParentid,teachplan.getParentid());
            Teachplan teachplan1 = teachplanMapper.selectOne(queryWrapper);
            teachplan1.setOrderby(teachplanCount + 1);
            teachplanMapper.updateById(teachplan1);
            teachplan.setOrderby(orderby-1);
            teachplanMapper.updateById(teachplan);
        }
    }

    @Override
    public void moveDown(Long teachplanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        int orderby = teachplan.getOrderby();
        int teachplanCount = getTeachplanCount(teachplanId, teachplan.getParentid());
        if (teachplanCount > 1) {
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getOrderby, teachplanCount - 1).eq(Teachplan::getParentid,teachplan.getParentid());
            Teachplan teachplan1 = teachplanMapper.selectOne(queryWrapper);
            teachplan1.setOrderby(teachplanCount + 1);
            teachplanMapper.updateById(teachplan1);
            teachplan.setOrderby(orderby-1);
            teachplanMapper.updateById(teachplan);
        }
    }

    @Transactional
    @Override
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        //教学计划的id
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        //约束校验
        //教学计划不存在无法绑定
        if(teachplan == null){
            OnlineEduException.cast("教学计划不存在");
        }
        //只有二级目录才可以绑定视频
        Integer grade = teachplan.getGrade();
        if(grade != 2){
            OnlineEduException.cast("只有二级目录才可以绑定视频");
        }

        //删除原来的绑定关系
        LambdaQueryWrapper<TeachplanMedia> lambdaQueryWrapper = new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId, teachplanId);
        teachplanMediaMapper.delete(lambdaQueryWrapper);

        //添加新的绑定关系
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMedia.setCourseId(teachplan.getCourseId());
        teachplanMediaMapper.insert(teachplanMedia);
        return teachplanMedia;
    }

    //计算机新课程计划的orderby 找到同级课程计划的数量 SELECT count(1) from teachplan where course_id=117 and parentid=268
    public int getTeachplanCount(Long courseId, Long parentId) {

        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        queryWrapper.eq(Teachplan::getParentid, parentId);

        return teachplanMapper.selectCount(queryWrapper);
    }
}

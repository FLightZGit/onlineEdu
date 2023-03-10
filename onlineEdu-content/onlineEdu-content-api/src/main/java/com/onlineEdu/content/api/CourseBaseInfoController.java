package com.onlineEdu.content.api;

import com.onlineEdu.base.model.PageParams;
import com.onlineEdu.base.model.PageResult;
import com.onlineEdu.content.model.dto.AddCourseDto;
import com.onlineEdu.content.model.dto.CourseBaseInfoDto;
import com.onlineEdu.content.model.dto.EditCourseDto;
import com.onlineEdu.content.model.dto.QueryCourseParamsDto;
import com.onlineEdu.content.model.po.CourseBase;
import com.onlineEdu.content.service.CourseBaseInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Astronaut
 * @version 1.0
 * @description
 * @date 2022/10/7 16:22
 */
@Api(value = "课程管理接口", tags = "课程管理接口")
@RestController
public class CourseBaseInfoController {

    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams params, @RequestBody QueryCourseParamsDto queryCourseParamsDto) {
        //调用service获取数据
        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(params, queryCourseParamsDto);

        return courseBasePageResult;
    }

    @ApiOperation("新增课程")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated AddCourseDto addCourseDto) {

        //获取当前用户所属培训机构的id
        Long companyId = 22L;

        //调用service
        CourseBaseInfoDto courseBase = courseBaseInfoService.createCourseBase(companyId, addCourseDto);
        return courseBase;
    }

    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId) {


        return courseBaseInfoService.getCourseBaseInfo(courseId);
    }

    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody EditCourseDto dto) {

        Long companyId = 1232141425L;
        return courseBaseInfoService.updateCourseBase(companyId, dto);
    }
}

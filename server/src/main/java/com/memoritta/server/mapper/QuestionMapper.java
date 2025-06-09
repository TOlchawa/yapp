package com.memoritta.server.mapper;

import com.memoritta.server.dao.QuestionDao;
import com.memoritta.server.model.Question;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
// This annotation indicates that we want to ignore unmapped target properties
// to avoid warnings for fields that are not present in both classes.
public interface QuestionMapper {
    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    QuestionDao toQuestionDao(Question question);
    Question toQuestion(QuestionDao dao);
}

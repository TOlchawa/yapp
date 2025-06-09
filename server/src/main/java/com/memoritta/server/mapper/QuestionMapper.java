package com.memoritta.server.mapper;

import com.memoritta.server.dao.QuestionDao;
import com.memoritta.server.model.Question;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface QuestionMapper {
    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    @Mapping(target = "answers", ignore = true)
    QuestionDao toQuestionDao(Question question);

    @Mapping(target = "answers", ignore = true)
    Question toQuestion(QuestionDao dao);
}

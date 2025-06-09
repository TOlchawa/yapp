package com.memoritta.server.mapper;

import com.memoritta.server.dao.QuestionDao;
import com.memoritta.server.model.Question;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(
    unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
    builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface QuestionMapper {
    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    QuestionDao toQuestionDao(Question question);
    Question toQuestion(QuestionDao dao);
}

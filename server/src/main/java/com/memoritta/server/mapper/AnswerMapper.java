package com.memoritta.server.mapper;

import com.memoritta.server.dao.AnswerDao;
import com.memoritta.server.model.Answer;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(
    unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
    builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface AnswerMapper {
    AnswerMapper INSTANCE = Mappers.getMapper(AnswerMapper.class);

    AnswerDao toAnswerDao(Answer answer);
    Answer toAnswer(AnswerDao dao);
}

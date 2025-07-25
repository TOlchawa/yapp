import { Body, Controller, Post } from '@nestjs/common';
import { IsString } from 'class-validator';
import { AiSummaryService } from './summary.service';

class SummaryRequest {
  @IsString()
  question!: string;
}

class SummaryResponse {
  summary!: string;
}

@Controller('questions')
export class SummaryController {
  constructor(private readonly service: AiSummaryService) {}

  @Post('summarize')
  async summarize(@Body() body: SummaryRequest): Promise<SummaryResponse> {
    const summary = await this.service.summarize(body.question);
    return { summary };
  }
}

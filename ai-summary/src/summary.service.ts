import { Injectable, Logger } from '@nestjs/common';
import OpenAI from 'openai';

@Injectable()
export class AiSummaryService {
  private readonly logger = new Logger(AiSummaryService.name);
  private readonly openai: OpenAI;
  private readonly prompt =
    'Summarize the question in 50 characters or less. Be concise.';

  constructor() {
    const apiKey = process.env.OPENAI_API_KEY || '';
    this.openai = new OpenAI({ apiKey });
  }

  private fallback(text: string): string {
    if (text.length <= 50) return text;
    return `${text.slice(0, 47)}...`;
  }

  async summarize(question: string): Promise<string> {
    try {
      const completion = await this.openai.chat.completions.create({
        model: 'gpt-4o',
        messages: [
          { role: 'system', content: this.prompt },
          { role: 'user', content: question },
        ],
        max_tokens: 50,
      });
      const summary = completion.choices[0]?.message?.content?.trim();
      if (!summary) throw new Error('empty summary');
      if (summary.length > 50) return this.fallback(summary);
      return summary;
    } catch (err) {
      this.logger.error('AI summarize failed', err as Error);
      return this.fallback(question);
    }
  }
}

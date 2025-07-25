import 'reflect-metadata';
import { NestFactory } from '@nestjs/core';
import { Module, ValidationPipe } from '@nestjs/common';
import { ExpressAdapter } from '@nestjs/platform-express';
import express from 'express';
import { AiSummaryService } from './summary.service';
import { SummaryController } from './summary.controller';

@Module({
  controllers: [SummaryController],
  providers: [AiSummaryService],
})
class AppModule {}

async function bootstrap() {
  const server = express();
  const app = await NestFactory.create(AppModule, new ExpressAdapter(server));
  app.useGlobalPipes(new ValidationPipe({ transform: true }));
  await app.init();
  return server;
}

if (require.main === module) {
  bootstrap().then((server) => {
    const port = process.env.PORT || 3000;
    server.listen(port, () => {
      console.log(`Listening on ${port}`);
    });
  });
}

export { bootstrap };

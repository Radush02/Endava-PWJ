import { Language, ProblemDTO } from "./problem.request";

export interface SubmitCodeDTO{
    problemId: number;
    source:string;
    language: Language
}

export interface SubmissionDTO{
    submissionId: string;
    problem: ProblemDTO;
    username: string;
    verdict: Verdict;
    output: string;
    expectedOutput: string;
    createdAt: Date;
    finishedAt: Date;
}

export enum Verdict {
  PENDING = "PENDING",
  RUNNING = "RUNNING",
  QUEUED = "QUEUED",
  AC = "AC",
  WA = "WA",
  TLE = "TLE",
  MLE = "MLE",
  RE = "RE",
  CE = "CE",
  IE = "IE"
}

export interface CreatedSubmissionDTO{
    submissionId: string;
    message: string;
}
import { CommentDTO } from "./comment.request";

export interface FullProblemDTO{
    id: number;
    title: string;
    description: string;
    difficulty: string;
    timeLimit: number;
    memoryLimit: number;
    author: string;
    comments: CommentDTO[];
}

export interface ProblemDTO{
    title: string;
    difficulty: Difficulty;
}
export enum Difficulty{
    EASY = 'Easy',
    MEDIUM = 'Medium',
    HARD = 'Hard'
}

export enum Language{
    CPP = 'CPP',
    JAVA = 'JAVA',
    PY = 'PY'
}
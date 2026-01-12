export interface AddCommentDTO {
    comment: string;
    problemId: number;
}

export interface CommentDTO{
    id: number;
    username: string;
    comment: string;
    upvotes: number;
    downvotes: number;
}

export interface AddCommentVoteDTO{
    commentId: number;
    voteType: CommentVoteType;
}

export enum CommentVoteType{
    UPVOTE="UPVOTE",
    DOWNVOTE="DOWNVOTE"
}
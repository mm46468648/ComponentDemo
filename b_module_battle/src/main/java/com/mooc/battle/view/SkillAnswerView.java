package com.mooc.battle.view;


import com.mooc.battle.model.GameFindResponse;
import com.mooc.battle.model.GameQuestion;
import com.mooc.battle.model.GameResultResponse;
import com.mooc.battle.model.GameViewAnswer;
import com.mooc.battle.model.SkillQuestionInfo;

public interface SkillAnswerView {


    void showPageDetail(SkillQuestionInfo gameFindResponse); //展示比武信息

//    void showResultPage(GameResultResponse data);  //展示结果页面

    void showResultPage();  //展示结果页面(去对战结果页显示数据,所以不传了)

    void showQuestion(int index,GameQuestion question); //展示问题

    void showCorrectAnswer(GameViewAnswer data);     //展示双方答案

    void showGameStatueError(String msg);  //展示游戏异常,因为接口中的错误

    void showNetWorkError();//因为网络环境的异常，导致游戏异常
}

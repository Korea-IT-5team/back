package com.project.back.common.object.board.inquiryboard;

import java.util.ArrayList;
import java.util.List;

import com.project.back.common.util.ChangeDateFormatUtil;
import com.project.back.repository.resultSet.GetInquiryBoardListResultSet;

import lombok.Getter;

@Getter
public class InquiryBoardListItem {
    private Integer inquiryNumber;
    private boolean status;
    private String inquiryTitle;
    private String inquiryWriterNickname;
    private String inquiryWriteDatetime;

    private InquiryBoardListItem(GetInquiryBoardListResultSet resultSets) throws Exception {
        this.inquiryNumber = resultSets.getInquiryNumber();
        this.status = resultSets.getStatus() == 1;
        this.inquiryTitle = resultSets.getInquiryTitle();
        this.inquiryWriterNickname = resultSets.getInquiryWriterNickname();

        String writeDate = ChangeDateFormatUtil.changeYYMMDD(resultSets.getInquiryWriteDatetime());
        this.inquiryWriteDatetime = writeDate;
    }

    public static List<InquiryBoardListItem> getList(List<GetInquiryBoardListResultSet> resultSets) throws Exception {
        List<InquiryBoardListItem> inquiryBoardList = new ArrayList<>();

        for(GetInquiryBoardListResultSet resultSet :resultSets) {
            InquiryBoardListItem inquiryBoardListItem = new InquiryBoardListItem(resultSet);
            inquiryBoardList.add(inquiryBoardListItem);
        }
        return inquiryBoardList;
    }
}

package com.project.back.dto.response.board.inquiryboard;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.project.back.common.util.ChangeDateFormatUtil;
import com.project.back.dto.response.ResponseCode;
import com.project.back.dto.response.ResponseDto;
import com.project.back.dto.response.ResponseMessage;
import com.project.back.entity.InquiryBoardEntity;

import lombok.Getter;

@Getter
public class GetInquiryBoardResponseDto extends ResponseDto{
    private Integer inquiryNumber;
    private String inquiryTitle;
    private String inquiryWriterId;
    private String inquiryWriterNickname;
    private String inquiryWriteDatetime;
    private String inquiryContents;
    private String comment;

    private GetInquiryBoardResponseDto(InquiryBoardEntity inquiryBoardEntity) throws Exception {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        this.inquiryNumber = inquiryBoardEntity.getInquiryNumber();
        this.inquiryTitle = inquiryBoardEntity.getInquiryTitle();
        this.inquiryWriterNickname = inquiryBoardEntity.getInquiryWriterNickname();

        String writeDate = ChangeDateFormatUtil.changeYYYYMMDD(inquiryBoardEntity.getInquiryWriteDatetime());
        this.inquiryWriteDatetime = writeDate;

        this.inquiryContents = inquiryBoardEntity.getInquiryContents();
        this.comment = inquiryBoardEntity.getInquiryComment();
    }

    public static ResponseEntity<GetInquiryBoardResponseDto> success(InquiryBoardEntity inquiryBoardEntity)
    throws Exception {
        GetInquiryBoardResponseDto responseBody = new GetInquiryBoardResponseDto(inquiryBoardEntity);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
}

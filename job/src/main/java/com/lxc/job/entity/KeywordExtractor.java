package com.lxc.job.entity;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.summary.TextRankKeyword;

import java.util.*;

import lombok.Data;

import java.util.List;

@Data
public class KeywordExtractor {

    public Set<String> getKeywords(String text){
        List<String> keywords = HanLP.extractKeyword(text, 3);
        Set<String> keySet = new HashSet<String>(keywords);
        return keySet;
    };

}

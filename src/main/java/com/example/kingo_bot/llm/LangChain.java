package com.example.kingo_bot.llm;

import com.hw.langchain.llms.openai.OpenAI;

public class LangChain {
    public static void print() {
        OpenAI llm = OpenAI.builder()
                .openaiApiKey("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")
                .requestTimeout(16)
                .build()
                .init();

        String result = llm.predict("What would be a good company name for a company that makes colorful socks?");
        System.out.println(result);
    }
}
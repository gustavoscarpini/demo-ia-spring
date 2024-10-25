package com.example.demoiaspring.aiservice;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
class AssistantTools {

//    /**
//     * This tool is available to {@link Assistant}
//     */
//    @Tool
//    String currentTime() {
//        return LocalTime.now().toString();
//    }


    @Tool("Retorna as cidades que existem lojas de pneu para antendimento")
    String getCities(
    ) {
        return "Maringá, Sarandí, Nova Andradina, São Tomé";
    }
//
    @Tool("Retorna os endereços das lojas para agendamento de manunteção")
    String getAdrress(
            @P("Nome da cidade que o endereço deve ser retornado") String city
    ) {
        return city != null && city.equalsIgnoreCase("Maringá") ? "Avenida Brasil 1222" : "Rua néo Alves martins 122";
    }
}

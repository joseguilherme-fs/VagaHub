package br.edu.ifpb.vagahub.services;

import br.edu.ifpb.vagahub.model.FrequenciaLembretes;
import br.edu.ifpb.vagahub.model.Lembrete;
import br.edu.ifpb.vagahub.model.Processo;
import br.edu.ifpb.vagahub.model.MailSender;
import br.edu.ifpb.vagahub.repository.LembreteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class LembreteService {

    @Autowired
    TaskScheduler taskScheduler;

    @Autowired
    LembreteRepository lembreteRepository;

    @Autowired
    EmailService emailService;

    public void formatarLembretes(Processo processo, LocalTime horario, String diaDaSemana, String frequenciaLembretes){
        List<Lembrete> lembretes = processo.getLembretes();

        switch (frequenciaLembretes) {
            case "nunca" -> {
                lembretes.clear();
            }
            case "por-semana" -> {
                lembretes.getFirst().setHorarioLembrete(horario);
                lembretes.getFirst().setDiaDaSemana(diaDaSemana);
                lembretes.getFirst().setFrequenciaLembretes(FrequenciaLembretes.SEMANALMENTE);
                lembretes.getFirst().setProcessoSeletivo(processo);

                lembreteRepository.save(lembretes.getFirst());

                lembretes.subList(1, lembretes.size()).clear();
            }
            case "datas-especificas" -> {
                for (int i = 0; i < lembretes.size(); ) {
                    Lembrete l = lembretes.get(i);
                    if (StringUtils.hasText(l.getDescricaoData()) && l.getDataLembrete() != null) {
                        l.setHorarioLembrete(horario);
                        l.setFrequenciaLembretes(FrequenciaLembretes.DATAS_ESPECIFICAS);
                        l.setProcessoSeletivo(processo);
                        lembreteRepository.save(l);

                        i++;
                    } else {
                        lembretes.remove(i);
                    }
                }
            }
        }
    }

    public void marcarLembretes(List<Lembrete> lembretes, String emailDestinatario, String nomeDestinatario, String titulo) throws ParseException {
        for(Lembrete l : lembretes){
            LocalTime horario = l.getHorarioLembrete();
            LocalDate data = l.getDataLembrete();
            String assunto = "";
            String mensagem = "";

            if(l.getFrequenciaLembretes() == FrequenciaLembretes.DATAS_ESPECIFICAS){
                assunto = String.format("Lembrete sobre '%s'", l.getDescricaoData());
                mensagem = String.format("""
                        Olá, %s.
                        
                        Viemos lembrá-lo sobre o evento '%s', relacionado à sua candidatura para a vaga '%s', que você marcou para hoje.
                        
                        Não esqueça de dar atenção à essa etapa do processo!
                        
                        Sucesso,
                        
                        Equipe VagaHub.
                        """, nomeDestinatario, l.getDescricaoData(), titulo);

                String dataFormatada = String.format("%d/%d/%d %d:%d",
                        data.getDayOfMonth(),
                        data.getMonthValue(),
                        data.getYear(),
                        horario.getHour(),
                        horario.getMinute()
                );
                taskScheduler.schedule(
                        new MailSender(emailDestinatario, assunto, mensagem, emailService),
                        new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(dataFormatada)
                );
            } else if (l.getFrequenciaLembretes() == FrequenciaLembretes.SEMANALMENTE) {
                assunto = String.format("Lembrete semanal sobre a vaga '%s'", titulo);
                mensagem = String.format("""
                        Olá, %s.
                        
                        Esse é o seu lembrete semanal sobre a vaga '%s'.
                        
                        Confira como está o andamento da sua candidatura.
                        
                        Sucesso,
                        
                        Equipe VagaHub.
                        """, nomeDestinatario, titulo);


                String cron = String.format("0 %d %d * * %s", horario.getMinute(), horario.getHour(), l.getDiaDaSemana());
                taskScheduler.schedule(
                        new MailSender(emailDestinatario, assunto, mensagem, emailService ),
                        new CronTrigger(cron)
                );
            }

        }
    }
}

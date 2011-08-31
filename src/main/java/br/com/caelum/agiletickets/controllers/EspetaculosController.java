package br.com.caelum.agiletickets.controllers;

import static br.com.caelum.vraptor.view.Results.status;

import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import br.com.caelum.agiletickets.domain.Agenda;
import br.com.caelum.agiletickets.domain.DiretorioDeEstabelecimentos;
import br.com.caelum.agiletickets.models.Espetaculo;
import br.com.caelum.agiletickets.models.Periodicidade;
import br.com.caelum.agiletickets.models.Sessao;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.ValidationMessage;

import com.google.common.base.Strings;

@Resource
public class EspetaculosController {

	private final Agenda agenda;
	private Validator validator;
	private Result result;

	private final DiretorioDeEstabelecimentos estabelecimentos;

	public EspetaculosController(Agenda agenda, DiretorioDeEstabelecimentos estabelecimentos, Validator validator, Result result) {
		this.agenda = agenda;
		this.estabelecimentos = estabelecimentos;
		this.validator = validator;
		this.result = result;
	}

	@Get @Path("/espetaculos")
	public List<Espetaculo> listaEspetaculos() {
		result.include("estabelecimentos", estabelecimentos.todos());
		return agenda.espetaculos();
	}

	@Post @Path("/espetaculos")
	public void adicionaEspetaculo(Espetaculo espetaculo) {
		validaNomeEspetaculo(espetaculo);
		validaDescricaoEspetaculo(espetaculo);
		validator.onErrorRedirectTo(this).listaEspetaculos();
		
		agenda.cadastra(espetaculo);
		result.redirectTo(this).listaEspetaculos();
	}

	private void validaDescricaoEspetaculo(Espetaculo espetaculo) {
		if (Strings.isNullOrEmpty(espetaculo.getDescricao())) {
			validator.add(new ValidationMessage("Descrição do espetáculo não pode estar em branco", ""));
		}
	}

	private void validaNomeEspetaculo(Espetaculo espetaculo) {
		if (Strings.isNullOrEmpty(espetaculo.getNome())) {
			validator.add(new ValidationMessage("Nome do espetáculo não pode estar em branco", ""));
		}
	}


	@Get @Path("/sessao/{id}")
	public void sessao(Long id) {
		Sessao sessao = agenda.sessao(id);
		existeSessao(sessao);

		result.include("sessao", sessao);
	}

	@Post @Path("/sessao/{sessaoId}/reserva")
	public void reservaLugaresNaSessao(Long sessaoId, final Integer quantidade) {
		Sessao sessao = agenda.sessao(sessaoId);
		existeSessao(sessao);
		
		validaQuantidadeRequisitada(quantidade);

		if (!sessao.podeReservar(quantidade)) {
			validator.add(new ValidationMessage("Não existem ingressos disponíveis", ""));
		}

		validator.onErrorRedirectTo(this).sessao(sessao.getId());

		sessao.reserva(quantidade);
		result.include("message", "Sessao reservada com sucesso");

		result.redirectTo(IndexController.class).index();
	}

	private void validaQuantidadeRequisitada(final Integer quantidade) {
		if (quantidade < 1) {
			validator.add(new ValidationMessage("Você deve escolher um lugar ou mais", ""));
		}
	}

	private void existeSessao(Sessao sessao) {
		if (sessao == null) {
			result.notFound();
		} 
	}

	@Get @Path("/espetaculo/{espetaculoId}/sessoes")
	public void sessoes(Long espetaculoId) {
		Espetaculo espetaculo = carregaEspetaculo(espetaculoId);

		result.include("espetaculo", espetaculo);
	}


	@Post @Path("/espetaculo/{espetaculoId}/sessoes")
	public void cadastraSessoes(Long espetaculoId, LocalDate inicio, LocalDate fim, LocalTime horario, Periodicidade periodicidade) {
		Espetaculo espetaculo = carregaEspetaculo(espetaculoId);

		List<Sessao> sessoes = espetaculo.criaSessoes(inicio, fim, horario, periodicidade);

		agenda.agende(sessoes);

		result.include("message", sessoes.size() + " sessoes criadas com sucesso");
		result.redirectTo(this).listaEspetaculos();
	}

	private Espetaculo carregaEspetaculo(Long espetaculoId) {
		Espetaculo espetaculo = agenda.espetaculo(espetaculoId);
		if (espetaculo == null) {
			validator.add(new ValidationMessage("", ""));
		}
		validator.onErrorUse(status()).notFound();
		return espetaculo;
	}

}

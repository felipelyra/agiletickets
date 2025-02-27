package br.com.caelum.agiletickets.controllers;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

//import antlr.collections.List;
import br.com.caelum.agiletickets.domain.Agenda;
import br.com.caelum.agiletickets.domain.DiretorioDeEstabelecimentos;
import br.com.caelum.agiletickets.models.Espetaculo;
import br.com.caelum.agiletickets.models.Periodicidade;
import br.com.caelum.agiletickets.models.Sessao;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;
import br.com.caelum.vraptor.validator.ValidationException;
import java.util.List;

public class EspetaculosControllerTest {


	private @Mock Agenda agenda;
	private @Mock DiretorioDeEstabelecimentos estabelecimentos;
	private @Spy Validator validator = new MockValidator();
	private @Spy Result result = new MockResult();
	private EspetaculosController controller;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new EspetaculosController(agenda, estabelecimentos, validator, result);
	}

	@Test(expected=ValidationException.class)
	public void naoDeveCadastrarEspetaculosSemNome() throws Exception {
		Espetaculo espetaculo = new Espetaculo();
		espetaculo.setDescricao("uma descricao");

		controller.adicionaEspetaculo(espetaculo);

		verifyZeroInteractions(agenda);
	}

	@Test(expected=ValidationException.class)
	public void naoDeveCadastrarEspetaculosSemDescricao() throws Exception {
		Espetaculo espetaculo = new Espetaculo();
		espetaculo.setNome("um nome");

		controller.adicionaEspetaculo(espetaculo);

		verifyZeroInteractions(agenda);
	}

	@Test
	public void deveCadastrarEspetaculosComNomeEDescricao() throws Exception {
		Espetaculo espetaculo = new Espetaculo();
		espetaculo.setNome("um nome");
		espetaculo.setDescricao("uma descricao");

		controller.adicionaEspetaculo(espetaculo);

		verify(agenda).cadastra(espetaculo);
	}

	@Test
	public void deveRetornarNotFoundSeASessaoNaoExiste() throws Exception {
		when(agenda.sessao(1234l)).thenReturn(null);

		controller.sessao(1234l);

		verify(result).notFound();
	}

	@Test(expected=ValidationException.class)
	public void naoDeveReservarZeroIngressos() throws Exception {
		when(agenda.sessao(1234l)).thenReturn(new Sessao());

		controller.reservaLugaresNaSessao(1234l, 0);

		verifyZeroInteractions(result);
	}

	@Test(expected=ValidationException.class)
	public void naoDeveReservarMaisIngressosQueASessaoPermite() throws Exception {
		Sessao sessao = new Sessao();
		sessao.setTotalIngressos(3);

		when(agenda.sessao(1234l)).thenReturn(sessao);

		controller.reservaLugaresNaSessao(1234l, 5);

		verifyZeroInteractions(result);
	}

	@Test
	public void deveReservarSeASessaoTemIngressosSuficientes() throws Exception {
		Sessao sessao = new Sessao();
		sessao.setTotalIngressos(5);

		when(agenda.sessao(1234l)).thenReturn(sessao);

		controller.reservaLugaresNaSessao(1234l, 3);

		assertThat(sessao.getIngressosDisponiveis(), is(2));
	}
	
	@Test
	public void naoDeveCadastrarSessaoSeInicioIgualFim() throws Exception {
		Espetaculo espetaculo = new Espetaculo(); //agenda.espetaculo(2L);
		
		LocalDate dataIgual = new LocalDate(2011, 8, 30);
		LocalTime horario = new LocalTime(12,00,00);
		
		//List retornoCriaSessao = espetaculo.criaSessoes(dataIgual, dataIgual, horario, Periodicidade.DIARIA);
		assertNull(espetaculo.criaSessoes(dataIgual, dataIgual, horario, Periodicidade.DIARIA));
	}
	
	@Test
	public void naoDeveCadastrarSessaoSeDataInicioMaiorQueDataFim() throws Exception {
		Espetaculo espetaculo = new Espetaculo(); //agenda.espetaculo(2L);
		
		LocalDate dataInicio = new LocalDate(2011, 8, 31);
		LocalDate dataFim = new LocalDate(2011, 8, 30);
		LocalTime horario = new LocalTime(12,00,00);
		
		//List retornoCriaSessao = espetaculo.criaSessoes(dataIgual, dataIgual, horario, Periodicidade.DIARIA);
		assertNull(espetaculo.criaSessoes(dataInicio, dataFim, horario, Periodicidade.DIARIA));
	}
	
	@Test
	public void deveCasdastrarDuasSessoes() throws Exception {
		Espetaculo espetaculo = new Espetaculo();
		LocalDate dataInicio = new LocalDate(2011, 9, 10);
		LocalDate dataFim = new LocalDate(2011, 9, 11);
		LocalTime horario = new LocalTime(12,00,00);
		
		List<Sessao> sessoesCriadas = espetaculo.criaSessoes(dataInicio, dataFim, horario,  Periodicidade.DIARIA);
 		
		assertThat(sessoesCriadas.size(), is(2));
	}
	
	@Test
	public void deveCasdastrarDuasSessoesComInicioDiferente() throws Exception {
		Espetaculo espetaculo = new Espetaculo();
		LocalDate dataInicio = new LocalDate(2011, 9, 10);
		LocalDate dataFim = new LocalDate(2011, 9, 11);
		LocalTime horario = new LocalTime(12,00,00);
		
		List<Sessao> sessoesCriadas = espetaculo.criaSessoes(dataInicio, dataFim, horario,  Periodicidade.DIARIA);
 		
		assertThat(sessoesCriadas.equals(dataInicio), not(true));
		
	}
	
}

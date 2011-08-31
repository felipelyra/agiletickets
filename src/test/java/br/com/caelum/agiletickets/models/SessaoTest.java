package br.com.caelum.agiletickets.models;

import org.junit.Test;
import org.junit.Before;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class SessaoTest {

	private Sessao sessao;
	
	@Before
	public void setUp() throws Exception {
		sessao = new Sessao();
	}
	
	@Test
	public void deveVender1ingressoSeHa2vagas() throws Exception {
		sessao.setTotalIngressos(2);

		assertThat(true, is(equalTo(sessao.podeReservar(1))));
	}

	@Test
	public void naoDeveVender3ingressoSeHa2vagas() throws Exception {
		sessao.setTotalIngressos(2);

		assertThat(false, is(equalTo(sessao.podeReservar(3))));
	}

	@Test
	public void reservarIngressosDeveDiminuirONumeroDeIngressosDisponiveis() throws Exception {
		sessao.setTotalIngressos(5);

		sessao.reserva(3);
		assertThat(2, is(equalTo(sessao.getIngressosDisponiveis().intValue())));
		
	}
	
	@Test
	public void deveVender1ingressoSeHa1vaga() throws Exception {
		sessao.setTotalIngressos(1);
		
		assertThat(true, is(equalTo(sessao.podeReservar(1))));
	}
}

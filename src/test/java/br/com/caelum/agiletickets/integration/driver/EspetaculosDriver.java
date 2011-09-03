package br.com.caelum.agiletickets.integration.driver;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class EspetaculosDriver {

	private static final String BASE_URL = "http://localhost:8080";
	private final WebDriver driver;

	public EspetaculosDriver(WebDriver driver) {
		this.driver = driver;
	}

	public void abreListagem() {
		driver.get(BASE_URL + "/espetaculos");
	}

	public void adicioneEspetaculos(String nome, String descricao, String tipo, String estabelecimento) {
		WebElement form = form();
		form.findElement(By.name("espetaculo.nome")).sendKeys(nome);
		form.findElement(By.name("espetaculo.descricao")).sendKeys(descricao);

		escolheItemDoSelectBox(tipo, form, "espetaculo.tipo");
		escolheItemDoSelectBox(estabelecimento, form, "espetaculo.estabelecimento.id");
		
		form.submit();
	}

	private void escolheItemDoSelectBox(String valor, WebElement form, String fieldName) {
		WebElement select = form.findElement(By.name(fieldName));
		List<WebElement> options = select.findElements(By.tagName("option"));
		for(WebElement option : options){
		    if(option.getText().equals(valor)){
		        option.setSelected();
		       }
		}
	}


	public void ultimaLinhaDeveConter(String nome, String descricao, String tipo) {
		WebElement ultimaLinha = ultimaLinha();
		assertThat(ultimaLinha.findElements(By.tagName("td")).get(1).getText(), is(nome));
		assertThat(ultimaLinha.findElements(By.tagName("td")).get(2).getText(), is(descricao));
		assertThat(ultimaLinha.findElements(By.tagName("td")).get(3).getText(), is(tipo));
	}
/*
	public void deveMostrarErro(String erro) {
		WebElement erros = driver.findElement(By.id("errors"));

		assertThat(erros.getText(), containsString(erro));
	}

	public void adicioneEstabelecimentoComEstacionamento(boolean temEstacionamento) {
		form().findElement(By.name("estabelecimento.temEstacionamento"))
			.sendKeys(temEstacionamento ? "Sim" : "Não");
		adicioneEstabelecimento("qualquer", "qualquer");
	}

	public void ultimaLinhaDeveTerEstacionamento(boolean estacionamento) {
		WebElement temEstacionamento = ultimaLinha().findElements(By.tagName("td")).get(3);
		assertThat(temEstacionamento.getText(), is(estacionamento ? "Sim" : "Não"));
	}
*/
	private WebElement form() {
		return driver.findElement(By.id("addForm"));
	}

	private WebElement ultimaLinha() {
		List<WebElement> linhas = driver.findElements(By.tagName("tr"));
		WebElement ultimaLinha = linhas.get(linhas.size() - 1);
		return ultimaLinha;
	}


}

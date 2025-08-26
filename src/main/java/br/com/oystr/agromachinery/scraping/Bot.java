package br.com.oystr.agromachinery.scraping;

public interface Bot {
    Machine fetch(String url);

    boolean supports(String url);
}

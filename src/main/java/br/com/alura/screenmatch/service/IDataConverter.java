package br.com.alura.screenmatch.service;

public interface IDataConverter {
    <T> T convertData(String data, Class<T> clazz);
}

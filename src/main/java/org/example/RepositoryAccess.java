package org.example;

public class RepositoryAccess {

    private static Repository repository;

    public static Repository getRepository() {
        if (repository == null) {
            repository = new Repository(new Connector());

        }
        return repository;

    }
}
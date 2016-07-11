package io.femo.http;

import io.femo.http.handlers.Authentication;
import io.femo.http.handlers.Handlers;
import io.femo.http.handlers.auth.CredentialProvider;

/**
 * Created by felix on 6/13/16.
 */
public class TestApp {

    public static void main(String[] args) {
        Http.server(8080)
                .use(Authentication.digest("test", new CredentialProvider() {
                    @Override
                    public Credentials findByUsername(String username) {
                        if("felix".equals(username)) {
                            return new Credentials("felix", "test");
                        }
                        return null;
                    }
                }))
                .get("/", new HttpHandler() {

                    @Override
                    public boolean handle(HttpRequest request, HttpResponse response) throws HttpHandleException {
                        response.entity("Hello World ${{time}}");
                        response.header("X-Replace-Env", "true");
                        return true;
                    }
                })
                .after(Handlers.environment())
                .after(Handlers.log())
                .start();
    }
}

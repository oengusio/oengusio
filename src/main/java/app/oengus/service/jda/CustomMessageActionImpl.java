package app.oengus.service.jda;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.restaction.MessageActionImpl;

public class CustomMessageActionImpl extends MessageActionImpl {
    public CustomMessageActionImpl(JDA api, Route.CompiledRoute route) {
        super(api, route, null);
    }

    @Override
    protected boolean hasPermission(Permission perm) {
        return true;
    }

    @Override
    protected void handleSuccess(Response response, Request<Message> request) {
        response.close();
        request.onSuccess(null);
    }
}

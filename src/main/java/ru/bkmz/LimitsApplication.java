package ru.bkmz;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import ru.bkmz.resources.ReceiveResource;

public class LimitsApplication extends Application<LimitsConfiguration> {

    public static void main(final String[] args) throws Exception {
        new LimitsApplication().run(args);
    }

    @Override
    public String getName() {
        return "limits";
    }

    @Override
    public void initialize(final Bootstrap<LimitsConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final LimitsConfiguration configuration,
                    final Environment environment) {
        environment.jersey().register(new ReceiveResource());
    }

}

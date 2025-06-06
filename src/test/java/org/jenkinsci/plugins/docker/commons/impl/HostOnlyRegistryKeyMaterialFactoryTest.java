/*
 * The MIT License
 *
 * Copyright 2018 Krassimir Valev.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkinsci.plugins.docker.commons.impl;

import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.emptyArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.jenkinsci.plugins.docker.commons.credentials.DockerRegistryEndpoint;
import org.jenkinsci.plugins.docker.commons.credentials.KeyMaterialContext;
import org.jenkinsci.plugins.docker.commons.credentials.KeyMaterialFactory;
import org.jenkinsci.plugins.docker.commons.tools.DockerTool;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.FakeLauncher;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.PretendSlave;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Launcher.ProcStarter;
import hudson.Proc;
import hudson.model.TaskListener;
import hudson.remoting.Callable;
import hudson.remoting.LocalChannel;
import hudson.remoting.VirtualChannel;

public class HostOnlyRegistryKeyMaterialFactoryTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private KeyMaterialFactory factory;

    @Test
    public void resolveRegistryDueToPodmanExecutable() throws Exception {
    	
	// fake launcher for the docker login invocation
	FakeLauncher faker = new FakeLauncher() {
	    @Override
	    public Proc onLaunch(final ProcStarter p) throws IOException {
		return new FinishedProc(0);
	    }
	};

	PretendSlave slave = j.createPretendSlave(faker);
	// VirtualChannel channel = slave.getChannel();
	// FreeStyleProject project = j.createFreeStyleProject();

	TaskListener listener = TaskListener.NULL;
	Launcher launcher = slave.createLauncher(listener);
	launcher = new Launcher.DecoratedLauncher(launcher) {
	    @Override
	    public VirtualChannel getChannel() {
		return new LocalChannel(null) {
		    @Override
		    public <V, T extends Throwable> V call(final Callable<V, T> callable) throws T {
			// ugly as hell, but we need a way to mock fetching the home directory
			return (V) new FilePath(tempFolder.getRoot());
		    }
		};
	    }
	};	

	URL endpoint = new DockerRegistryEndpoint(null, null).getEffectiveUrl();
	EnvVars env = new EnvVars();
	String dockerExecutable = "/usr/bin/podman";

	factory = new RegistryKeyMaterialFactory("username", "password", endpoint, launcher, env, listener,
		dockerExecutable).contextualize(new KeyMaterialContext(new FilePath(tempFolder.newFolder())));
	// act
	String registry = ((RegistryKeyMaterialFactory)factory).registry();

	// assert
	assertEquals(registry, "index.docker.io");
    }
    
    @Test
    public void resolveRegistryDueToENVSet() throws Exception {
    	
	// fake launcher for the docker login invocation
	FakeLauncher faker = new FakeLauncher() {
	    @Override
	    public Proc onLaunch(final ProcStarter p) throws IOException {
		return new FinishedProc(0);
	    }
	};

	PretendSlave slave = j.createPretendSlave(faker);
	// VirtualChannel channel = slave.getChannel();
	// FreeStyleProject project = j.createFreeStyleProject();

	TaskListener listener = TaskListener.NULL;
	Launcher launcher = slave.createLauncher(listener);
	launcher = new Launcher.DecoratedLauncher(launcher) {
	    @Override
	    public VirtualChannel getChannel() {
		return new LocalChannel(null) {
		    @Override
		    public <V, T extends Throwable> V call(final Callable<V, T> callable) throws T {
			// ugly as hell, but we need a way to mock fetching the home directory
			return (V) new FilePath(tempFolder.getRoot());
		    }
		};
	    }
	};	

	URL endpoint = new DockerRegistryEndpoint(null, null).getEffectiveUrl();
	EnvVars env = new EnvVars();
	env.put(RegistryKeyMaterialFactory.DOCKER_REGISTRY_HOST_ONLY, "true");
	String dockerExecutable = DockerTool.getExecutable(null, null, listener, env);

	factory = new RegistryKeyMaterialFactory("username", "password", endpoint, launcher, env, listener,
		dockerExecutable).contextualize(new KeyMaterialContext(new FilePath(tempFolder.newFolder())));
	// act
	String registry = ((RegistryKeyMaterialFactory)factory).registry();

	// assert
	assertEquals(registry, "index.docker.io");
    }

    @Test
    public void resolveRegistryDefault() throws Exception {
    	
	// fake launcher for the docker login invocation
	FakeLauncher faker = new FakeLauncher() {
	    @Override
	    public Proc onLaunch(final ProcStarter p) throws IOException {
		return new FinishedProc(0);
	    }
	};

	PretendSlave slave = j.createPretendSlave(faker);
	// VirtualChannel channel = slave.getChannel();
	// FreeStyleProject project = j.createFreeStyleProject();

	TaskListener listener = TaskListener.NULL;
	Launcher launcher = slave.createLauncher(listener);
	launcher = new Launcher.DecoratedLauncher(launcher) {
	    @Override
	    public VirtualChannel getChannel() {
		return new LocalChannel(null) {
		    @Override
		    public <V, T extends Throwable> V call(final Callable<V, T> callable) throws T {
			// ugly as hell, but we need a way to mock fetching the home directory
			return (V) new FilePath(tempFolder.getRoot());
		    }
		};
	    }
	};	

	URL endpoint = new DockerRegistryEndpoint(null, null).getEffectiveUrl();
	EnvVars env = new EnvVars();
	String dockerExecutable = DockerTool.getExecutable(null, null, listener, env);

	factory = new RegistryKeyMaterialFactory("username", "password", endpoint, launcher, env, listener,
		dockerExecutable).contextualize(new KeyMaterialContext(new FilePath(tempFolder.newFolder())));
	// act
	String registry = ((RegistryKeyMaterialFactory)factory).registry();

	// assert
	assertEquals(registry, "https://index.docker.io/v1/");
    }
}

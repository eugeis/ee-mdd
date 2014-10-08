/*
* Copyright 2011-2012 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package ee.mdd.generator;

import groovy.util.logging.Slf4j

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 *
 * @author Eugen Eisler
 */
@Slf4j
class Executor {
	ExecutorService pool

	Executor(threads = 5) {
		pool = Executors.newFixedThreadPool(threads)
	}

	void submit(Closure closure) {
		Callable c = closure as Callable
		pool.submit(c)
	}

	void shutdownAndAwaitTermination() {
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(10, TimeUnit.MINUTES)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(60, TimeUnit.SECONDS))
					log.error('Pool did not terminate');
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}
}

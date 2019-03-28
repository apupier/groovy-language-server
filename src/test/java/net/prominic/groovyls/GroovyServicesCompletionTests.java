////////////////////////////////////////////////////////////////////////////////
// Copyright 2019 Prominic.NET, Inc.
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
// http://www.apache.org/licenses/LICENSE-2.0 
// 
// Unless required by applicable law or agreed to in writing, software 
// distributed under the License is distributed on an "AS IS" BASIS, 
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and 
// limitations under the License
// 
// Author: Prominic.NET, Inc.
// No warranty of merchantability or fitness of any kind. 
// Use this software at your own risk.
////////////////////////////////////////////////////////////////////////////////
package net.prominic.groovyls;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.MessageActionItem;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.ShowMessageRequestParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GroovyServicesCompletionTests {
	private static final String LANGUAGE_GROOVY = "groovy";

	private GroovyServices services;
	private Path workspaceRoot;

	@BeforeEach
	void setup() {
		workspaceRoot = Paths.get("./test_workspace");
		services = new GroovyServices();
		services.setWorkspaceRoot(workspaceRoot);
		services.connect(new LanguageClient() {

			@Override
			public void telemetryEvent(Object object) {

			}

			@Override
			public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams requestParams) {
				return null;
			}

			@Override
			public void showMessage(MessageParams messageParams) {

			}

			@Override
			public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {

			}

			@Override
			public void logMessage(MessageParams message) {

			}
		});
	}

	@AfterEach
	void tearDown() {
		services = null;
	}

	@Test
	void testMemberAccessOnLocalVariable() throws Exception {
		Path filePath = workspaceRoot.resolve("./src/main/java/Completion.groovy");
		String uri = filePath.toUri().toString();
		StringBuilder contents = new StringBuilder();
		contents.append("class Completion {\n");
		contents.append("  public Completion() {\n");
		contents.append("    String localVar\n");
		contents.append("    localVar.\n");
		contents.append("  }\n");
		contents.append("}");
		TextDocumentItem textDocumentItem = new TextDocumentItem(uri, LANGUAGE_GROOVY, 1, contents.toString());
		services.didOpen(new DidOpenTextDocumentParams(textDocumentItem));
		TextDocumentIdentifier textDocument = new TextDocumentIdentifier(uri);
		Position position = new Position(3, 13);
		Either<List<CompletionItem>, CompletionList> result = services
				.completion(new CompletionParams(textDocument, position)).get();
		Assertions.assertTrue(result.isLeft());
		List<CompletionItem> items = result.getLeft();
		Assertions.assertTrue(items.size() > 0);
		List<CompletionItem> filteredItems = items.stream().filter(item -> {
			return item.getLabel().equals("charAt") && item.getKind().equals(CompletionItemKind.Method);
		}).collect(Collectors.toList());
		Assertions.assertTrue(filteredItems.size() > 0);
	}

	@Test
	void testMemberAccessOnMemberVariable() throws Exception {
		Path filePath = workspaceRoot.resolve("./src/main/java/Completion.groovy");
		String uri = filePath.toUri().toString();
		StringBuilder contents = new StringBuilder();
		contents.append("class Completion {\n");
		contents.append("  String memberVar\n");
		contents.append("  public Completion() {\n");
		contents.append("    memberVar.\n");
		contents.append("  }\n");
		contents.append("}");
		TextDocumentItem textDocumentItem = new TextDocumentItem(uri, LANGUAGE_GROOVY, 1, contents.toString());
		services.didOpen(new DidOpenTextDocumentParams(textDocumentItem));
		TextDocumentIdentifier textDocument = new TextDocumentIdentifier(uri);
		Position position = new Position(3, 14);
		Either<List<CompletionItem>, CompletionList> result = services
				.completion(new CompletionParams(textDocument, position)).get();
		Assertions.assertTrue(result.isLeft());
		List<CompletionItem> items = result.getLeft();
		Assertions.assertTrue(items.size() > 0);
		List<CompletionItem> filteredItems = items.stream().filter(item -> {
			return item.getLabel().equals("charAt") && item.getKind().equals(CompletionItemKind.Method);
		}).collect(Collectors.toList());
		Assertions.assertTrue(filteredItems.size() > 0);
	}

	@Test
	void testMemberAccessOnThis() throws Exception {
		Path filePath = workspaceRoot.resolve("./src/main/java/Completion.groovy");
		String uri = filePath.toUri().toString();
		StringBuilder contents = new StringBuilder();
		contents.append("class Completion {\n");
		contents.append("  String memberVar\n");
		contents.append("  public Completion() {\n");
		contents.append("    this.\n");
		contents.append("  }\n");
		contents.append("}");
		TextDocumentItem textDocumentItem = new TextDocumentItem(uri, LANGUAGE_GROOVY, 1, contents.toString());
		services.didOpen(new DidOpenTextDocumentParams(textDocumentItem));
		TextDocumentIdentifier textDocument = new TextDocumentIdentifier(uri);
		Position position = new Position(3, 9);
		Either<List<CompletionItem>, CompletionList> result = services
				.completion(new CompletionParams(textDocument, position)).get();
		Assertions.assertTrue(result.isLeft());
		List<CompletionItem> items = result.getLeft();
		Assertions.assertTrue(items.size() > 0);
		List<CompletionItem> filteredItems = items.stream().filter(item -> {
			return item.getLabel().equals("memberVar") && item.getKind().equals(CompletionItemKind.Field);
		}).collect(Collectors.toList());
		Assertions.assertTrue(filteredItems.size() > 0);
	}

	@Test
	void testMemberAccessOnClass() throws Exception {
		Path filePath = workspaceRoot.resolve("./src/main/java/Completion.groovy");
		String uri = filePath.toUri().toString();
		StringBuilder contents = new StringBuilder();
		contents.append("class Completion {\n");
		contents.append("  public Completion() {\n");
		contents.append("    Completion.\n");
		contents.append("  }\n");
		contents.append("  public static void staticMethod() {}\n");
		contents.append("}");
		TextDocumentItem textDocumentItem = new TextDocumentItem(uri, LANGUAGE_GROOVY, 1, contents.toString());
		services.didOpen(new DidOpenTextDocumentParams(textDocumentItem));
		TextDocumentIdentifier textDocument = new TextDocumentIdentifier(uri);
		Position position = new Position(2, 15);
		Either<List<CompletionItem>, CompletionList> result = services
				.completion(new CompletionParams(textDocument, position)).get();
		Assertions.assertTrue(result.isLeft());
		List<CompletionItem> items = result.getLeft();
		Assertions.assertTrue(items.size() > 0);
		List<CompletionItem> filteredItems = items.stream().filter(item -> {
			return item.getLabel().equals("staticMethod") && item.getKind().equals(CompletionItemKind.Method);
		}).collect(Collectors.toList());
		Assertions.assertTrue(filteredItems.size() > 0);
	}

	@Test
	void testMemberAccessWithExistingPropertyExpression() throws Exception {
		Path filePath = workspaceRoot.resolve("./src/main/java/Completion.groovy");
		String uri = filePath.toUri().toString();
		StringBuilder contents = new StringBuilder();
		contents.append("class Completion {\n");
		contents.append("  public Completion() {\n");
		contents.append("    String localVar\n");
		contents.append("    localVar.\n");
		contents.append("    localVar\n");
		contents.append("  }\n");
		contents.append("}");
		TextDocumentItem textDocumentItem = new TextDocumentItem(uri, LANGUAGE_GROOVY, 1, contents.toString());
		services.didOpen(new DidOpenTextDocumentParams(textDocumentItem));
		TextDocumentIdentifier textDocument = new TextDocumentIdentifier(uri);
		Position position = new Position(3, 13);
		Either<List<CompletionItem>, CompletionList> result = services
				.completion(new CompletionParams(textDocument, position)).get();
		Assertions.assertTrue(result.isLeft());
		List<CompletionItem> items = result.getLeft();
		Assertions.assertTrue(items.size() > 0);
		List<CompletionItem> filteredItems = items.stream().filter(item -> {
			return item.getLabel().equals("charAt") && item.getKind().equals(CompletionItemKind.Method);
		}).collect(Collectors.toList());
		Assertions.assertTrue(filteredItems.size() > 0);
	}

	@Test
	void testMemberAccessWithExistingMethodCallExpression() throws Exception {
		Path filePath = workspaceRoot.resolve("./src/main/java/Completion.groovy");
		String uri = filePath.toUri().toString();
		StringBuilder contents = new StringBuilder();
		contents.append("class Completion {\n");
		contents.append("  public Completion() {\n");
		contents.append("    String localVar\n");
		contents.append("    localVar.\n");
		contents.append("    method()\n");
		contents.append("  }\n");
		contents.append("}");
		TextDocumentItem textDocumentItem = new TextDocumentItem(uri, LANGUAGE_GROOVY, 1, contents.toString());
		services.didOpen(new DidOpenTextDocumentParams(textDocumentItem));
		TextDocumentIdentifier textDocument = new TextDocumentIdentifier(uri);
		Position position = new Position(3, 13);
		Either<List<CompletionItem>, CompletionList> result = services
				.completion(new CompletionParams(textDocument, position)).get();
		Assertions.assertTrue(result.isLeft());
		List<CompletionItem> items = result.getLeft();
		Assertions.assertTrue(items.size() > 0);
		List<CompletionItem> filteredItems = items.stream().filter(item -> {
			return item.getLabel().equals("charAt") && item.getKind().equals(CompletionItemKind.Method);
		}).collect(Collectors.toList());
		Assertions.assertTrue(filteredItems.size() > 0);
	}
}
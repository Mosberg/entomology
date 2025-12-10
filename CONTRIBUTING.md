# Contributing to Entomology

Thank you for your interest in contributing! This document provides guidelines for contributing to the project.

## 🎯 Development Setup

### Prerequisites

- Java 21 or higher
- Git
- IDE (IntelliJ IDEA recommended)

### Setup Steps

1. **Fork and Clone**

   ```bash
   git clone https://github.com/mosberg/entomology-json.git
   cd entomology-json
   ```

2. **Generate IDE Files**

   ```bash
   ./gradlew genSources
   ./gradlew idea  # For IntelliJ
   # OR
   ./gradlew eclipse  # For Eclipse
   ```

3. **Run the Mod**

   ```bash
   ./gradlew runClient
   ```

4. **Run Tests**
   ```bash
   ./gradlew test
   ```

## 📐 Code Style

We follow standard Java conventions with some additions:

### Naming Conventions

- **Classes**: `PascalCase` (e.g., `BreedingMechanic`)
- **Methods**: `camelCase` (e.g., `getSpecimen`)
- **Constants**: `UPPER_SNAKE_CASE` (e.g., `MAX_RARITY`)
- **Packages**: `lowercase` (e.g., `mechanics`)

### Code Structure

```java
/**
 * Javadoc for public classes/methods.
 * Include @param, @return, @throws as needed.
 */
public class Example {
    // Constants first
    private static final int CONSTANT = 10;

    // Fields
    private int field;

    // Constructor
    public Example() {}

    // Public methods
    public void method() {}

    // Private methods
    private void helper() {}
}
```

### Best Practices

- ✅ **DO** add Javadoc to all public APIs
- ✅ **DO** write unit tests for new features
- ✅ **DO** handle errors gracefully
- ✅ **DO** log important events
- ❌ **DON'T** leave commented-out code
- ❌ **DON'T** use magic numbers (use constants)
- ❌ **DON'T** catch Exception without good reason

## 🏗️ Architecture Guidelines

### Adding a New Mechanic

1. **Create the mechanic class**

   ```java
   public class MyMechanic implements IMechanic {
       @Override
       public Identifier getId() {
           return EntomologyMod.id("my_mechanic");
       }

       // Implement other methods...
   }
   ```

2. **Register in ModRegistry**

   ```java
   api.registerMechanic(new MyMechanic());
   ```

3. **Add configuration schema**

   - Create `docs/schemas/my_mechanic-schema.json`

4. **Write tests**
   ```java
   @Test
   void testMyMechanic() {
       MyMechanic mechanic = new MyMechanic();
       assertTrue(mechanic.appliesTo("test", context));
   }
   ```

### Adding a Component

1. **Implement ISpecimenComponent**
2. **Create factory function**
3. **Register via API**
4. **Document in ARCHITECTURE.md**

### Modifying Data Formats

When changing JSON schemas:

1. Update schema file in `docs/schemas/`
2. Update validator class
3. Migrate existing JSON files
4. Document breaking changes
5. Bump API version if needed

## 🧪 Testing Requirements

### Unit Tests

- All new mechanics must have tests
- Test both success and failure cases
- Mock external dependencies

### Integration Tests

- Test mechanic interactions
- Test config loading/reloading
- Test JSON validation

### Manual Testing

1. Build the mod: `./gradlew build`
2. Run client: `./gradlew runClient`
3. Test in-game functionality
4. Check logs for errors

## 📝 Pull Request Process

### Before Submitting

- [ ] Code compiles without errors
- [ ] All tests pass
- [ ] Added tests for new features
- [ ] Updated documentation
- [ ] Followed code style guidelines
- [ ] No merge conflicts with main

### PR Template

```markdown
## Description

Brief description of changes

## Type of Change

- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing

How has this been tested?

## Checklist

- [ ] Tests pass
- [ ] Documentation updated
- [ ] No compilation warnings
```

### Review Process

1. Automated checks run (CI/CD)
2. Code review by maintainer
3. Address feedback
4. Merge when approved

## 🐛 Bug Reports

### Good Bug Report Includes

- Minecraft version
- Mod version
- Fabric Loader version
- Steps to reproduce
- Expected vs actual behavior
- Relevant logs
- Crash report if applicable

### Template

```markdown
**Environment**

- Minecraft: 1.21.10
- Mod Version: 1.0.0
- Fabric Loader: 0.18.1

**Description**
Clear description of the bug

**Steps to Reproduce**

1. Step one
2. Step two
3. See error

**Expected Behavior**
What should happen

**Actual Behavior**
What actually happens

**Logs**
```

Paste relevant logs here

```

```

## 💡 Feature Requests

### Good Feature Request Includes

- Clear use case
- Proposed solution
- Alternative solutions considered
- Implementation complexity estimate

## 🔄 Release Process

Maintainers only:

1. **Update version in gradle.properties**
2. **Update CHANGELOG.md**
3. **Commit changes**
   ```bash
   git commit -m "Release version 1.0.1"
   ```
4. **Create tag**
   ```bash
   git tag -a v1.0.1 -m "Release 1.0.1"
   ```
5. **Push**
   ```bash
   git push origin main --tags
   ```
6. **GitHub Actions handles the rest**

## 📚 Documentation

### What to Document

- All public API classes/methods
- Architecture decisions
- Configuration options
- JSON schemas
- Migration guides for breaking changes

### Where to Document

- Code: Javadoc comments
- User-facing: README.md
- Developer-facing: ARCHITECTURE.md
- Schemas: docs/schemas/
- Examples: docs/examples/

## 🤝 Community Guidelines

- Be respectful and inclusive
- Help others learn
- Give constructive feedback
- Credit contributors
- Follow the [Code of Conduct](CODE_OF_CONDUCT.md)

## 📫 Contact

- GitHub Issues: Bug reports and features
- GitHub Discussions: Questions and ideas
- Discord: [Link if available]

## 📄 License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

Thank you for contributing to Entomology! 🐛🦋

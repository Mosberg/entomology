# Contributing to Entomology Mod

Thank you for your interest in contributing to the Entomology mod! This guide will help you get started.

## Code of Conduct

- Be respectful and inclusive
- Provide constructive feedback
- Help newcomers
- Follow project guidelines

## Getting Started

### Prerequisites

- Java 21 or higher
- Gradle 8.x
- Git
- IDE with Minecraft modding support (IntelliJ IDEA recommended)

### Setup Development Environment

1. **Clone the repository**:

   ```bash
   git clone https://github.com/Mosberg/entomology.git
   cd entomology
   ```

2. **Generate IDE files**:

   ```bash
   # For IntelliJ IDEA
   gradlew idea

   # For Eclipse
   gradlew eclipse
   ```

3. **Build the mod**:

   ```bash
   gradlew build
   ```

4. **Run in development**:
   ```bash
   gradlew runClient
   ```

## Project Structure

```
entomology/
├── src/
│   ├── main/
│   │   ├── java/           # Source code
│   │   │   └── dk/mosberg/entomology/
│   │   │       ├── api/           # Public APIs
│   │   │       ├── mechanics/     # Gameplay mechanics
│   │   │       ├── config/        # Configuration system
│   │   │       ├── registry/      # Component registry
│   │   │       ├── balance/       # Balance tuning
│   │   │       └── ...
│   │   └── resources/      # Assets and data
│   │       ├── assets/            # Client-side resources
│   │       └── data/              # Server-side data
│   ├── test/               # Unit tests
│   └── client/             # Client-specific code
├── docs/                   # Documentation
├── build.gradle            # Build configuration
└── gradle.properties       # Project properties
```

## Contribution Types

### 1. Bug Fixes

- Search existing issues first
- Create issue if not exists
- Reference issue in PR
- Include reproduction steps
- Add regression test

### 2. New Features

- Discuss in issue or Discord first
- Follow data-driven design principles
- Add comprehensive tests
- Update documentation
- Provide configuration examples

### 3. Data-Driven Content

- Add JSON definitions
- Follow existing schemas
- Validate against schemas
- Test hot-reload
- Add translations

### 4. Documentation

- Fix typos and errors
- Add examples
- Improve clarity
- Keep up-to-date
- Add diagrams where helpful

## Development Guidelines

### Code Style

We use Checkstyle to enforce consistent code style:

```bash
gradlew checkstyleMain
gradlew checkstyleTest
```

**Key conventions**:

- Use 2 spaces for indentation
- Maximum line length: 100 characters
- Use descriptive variable names
- Add JavaDoc for public APIs
- Follow Java naming conventions

### Architecture Principles

1. **Separation of Concerns**: Keep layers independent
2. **Data-Driven**: Prefer configuration over code
3. **Extensibility**: Design for plugins and extensions
4. **Performance**: Profile and optimize hot paths
5. **Thread Safety**: Use concurrent data structures

### API Design

**Public APIs must**:

- Be well-documented with JavaDoc
- Include usage examples
- Support versioning
- Maintain backward compatibility
- Be thread-safe

**Example**:

````java
/**
 * Registers a custom mechanic with the system.
 *
 * <p>Mechanics are executed in priority order, with higher priority
 * mechanics running first. Dependencies are automatically resolved.
 *
 * @param mechanic the mechanic to register
 * @throws IllegalArgumentException if mechanic is null or already registered
 * @since 2.0.0
 * @see IAdvancedMechanic
 *
 * @example
 * ```java
 * api.registerMechanic(new CustomMechanic());
 * ```
 */
public void registerMechanic(IAdvancedMechanic mechanic);
````

### Testing Requirements

All contributions must include tests:

#### Unit Tests

- Test individual components
- Mock external dependencies
- Aim for 80%+ coverage
- Use descriptive test names

```java
@Test
void testBreedingWithValidPair() {
    // Given
    BreedingMechanic mechanic = new BreedingMechanic();
    IMechanicContext context = createBreedingContext();

    // When
    IMechanicResult result = mechanic.execute(context);

    // Then
    assertTrue(result.isSuccess());
    assertEquals("offspring_id", result.getData("offspring").orElse(null));
}
```

#### Integration Tests

- Test component interactions
- Verify lifecycle management
- Test configuration loading
- Validate data-driven behavior

#### Performance Tests

- Benchmark critical paths
- Ensure no regressions
- Test with large datasets

### Running Tests

```bash
# Run all tests
gradlew test

# Run specific test class
gradlew test --tests ComponentRegistryTest

# Run with coverage
gradlew test jacocoTestReport
```

## Data Contribution Guidelines

### Adding New Specimens

1. **Create JSON file** in `src/main/resources/data/entomology/specimen/`:

```json
{
  "version": "1.0.0",
  "id": "mymod:custom_beetle",
  "displayName": "specimen.mymod.custom_beetle",
  "description": "specimen.mymod.custom_beetle.desc",
  "rarity": "uncommon",
  "spawnBiomes": ["minecraft:forest"],
  "spawnWeight": 8,
  "traits": {
    "size": {
      "level": 5,
      "inheritable": true
    }
  }
}
```

2. **Add translations** in `src/main/resources/assets/entomology/lang/en_us.json`:

```json
{
  "specimen.mymod.custom_beetle": "Custom Beetle",
  "specimen.mymod.custom_beetle.desc": "A unique beetle species"
}
```

3. **Validate against schema**:

```bash
# Schema validation runs automatically during build
gradlew validateData
```

4. **Test in-game**:

```bash
gradlew runClient
```

### Adding Breeding Pairs

Edit `src/main/resources/data/entomology/mechanics/breeding_config.json`:

```json
{
  "breedingPairs": [
    {
      "parent1": "mymod:custom_beetle",
      "parent2": "entomology:beetle",
      "offspring": ["mymod:hybrid_beetle"],
      "successChance": 0.3,
      "mutationChance": 0.05
    }
  ]
}
```

### Adding Custom Mechanics

1. **Create mechanic class**:

```java
package dk.mosberg.entomology.mechanics.impl;

public class CustomMechanic extends AbstractMechanic {
    public CustomMechanic() {
        super(
            EntomologyMod.id("custom"),
            "1.0.0",
            MechanicCategory.CUSTOM,
            500
        );
    }

    // Implement required methods
}
```

2. **Register in ModRegistry**:

```java
ComponentRegistry.getInstance().register(
    EntomologyMod.id("custom_mechanic"),
    IAdvancedMechanic.class,
    CustomMechanic::new
);
```

3. **Add configuration**:

Create `data/entomology/mechanics/custom_config.json`

4. **Add tests**:

Create `test/java/.../CustomMechanicTest.java`

## Pull Request Process

### Before Submitting

- [ ] Code follows style guidelines
- [ ] All tests pass
- [ ] Added tests for new features
- [ ] Documentation updated
- [ ] Checkstyle passes
- [ ] No merge conflicts
- [ ] Commit messages are descriptive

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

How was this tested?

## Checklist

- [ ] Tests added/updated
- [ ] Documentation updated
- [ ] Changelog updated
- [ ] Follows code style

## Related Issues

Fixes #123
```

### Review Process

1. **Automated checks** run on every PR
2. **Code review** by maintainers
3. **Testing** on various platforms
4. **Feedback** and iteration
5. **Merge** when approved

### Commit Message Format

Use conventional commits:

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types**:

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Code style
- `refactor`: Code refactoring
- `test`: Tests
- `chore`: Maintenance

**Examples**:

```
feat(breeding): add trait inheritance system

Implements advanced trait inheritance with dominant/recessive genes
and mutation support. Fully configurable via JSON.

Closes #123
```

## Communication

### Discord Server

Join our Discord for:

- Development discussion
- Design feedback
- Community support
- Early testing

### GitHub Issues

Use for:

- Bug reports
- Feature requests
- Technical discussion

### GitHub Discussions

Use for:

- General questions
- Ideas and proposals
- Community showcase

## Release Process

### Versioning

We follow [Semantic Versioning](https://semver.org/):

- **Major** (X.0.0): Breaking changes
- **Minor** (1.X.0): New features, backward compatible
- **Patch** (1.0.X): Bug fixes

### Release Checklist

- [ ] Version bumped
- [ ] Changelog updated
- [ ] All tests passing
- [ ] Documentation current
- [ ] Migration guide (if breaking)
- [ ] Release notes written

## Performance Guidelines

### Profiling

Use built-in performance metrics:

```java
IPerformanceMetrics metrics = mechanic.getPerformanceMetrics();
System.out.println("Avg execution time: " + metrics.getAverageExecutionTime());
```

### Optimization Tips

1. **Avoid in hot loops**:

   - String concatenation
   - Excessive object creation
   - Reflection

2. **Use caching**:

   - Frequently accessed data
   - Computed values
   - Validation results

3. **Lazy initialization**:

   - Expensive resources
   - Optional features

4. **Batch operations**:
   - Multiple updates
   - Data loading

## Security

### Reporting Security Issues

**DO NOT** create public issues for security vulnerabilities.

Email security concerns to: [security@example.com]

Include:

- Description of vulnerability
- Steps to reproduce
- Potential impact
- Suggested fix (if any)

### Security Best Practices

- Validate all user input
- Sanitize file paths
- Use safe JSON parsing
- Limit resource consumption
- Follow principle of least privilege

## License

By contributing, you agree that your contributions will be licensed under the project's [MIT License](../LICENSE).

## Recognition

Contributors are recognized in:

- CONTRIBUTORS.md file
- Release notes
- In-game credits

Thank you for contributing to Entomology!

## Questions?

- **Discord**: [Join our server]
- **Email**: support@example.com
- **Issues**: GitHub issue tracker

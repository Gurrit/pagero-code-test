# email-client (provided)

A legacy email-sending library. **Do not modify.**

## API

```java
public final class EmailClient {
    public void send(String from, String to, String subject, String body);
}
```

`send` will throw `IllegalArgumentException` for blank `from`/`to` or null
`subject`/`body`. Otherwise it logs the email at INFO level and returns.

The candidate's job is to use this as-is — wrap it however you like, but
don't touch the source.

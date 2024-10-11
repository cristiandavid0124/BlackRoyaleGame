module.exports = {
  creds: {
    identityMetadata: 'https://login.microsoftonline.com/4d4d334a-9797-4b89-b8e9-28946972b8f3/v2.0/.well-known/openid-configuration', // Reemplaza TU_TENANT_ID con tu tenant
    clientID: 'a18be00e-d104-4272-b36c-7a05bee4eff4', // El ID de aplicación del backend registrado en Microsoft Entra ID
    validateIssuer: true,
    issuer: 'https://sts.windows.net/4d4d334a-9797-4b89-b8e9-28946972b8f3/', // Issuer que debe coincidir con el tenant
    passReqToCallback: false,
    loggingLevel: 'info',
  },
  resource: 'api://a18be00e-d104-4272-b36c-7a05bee4eff4', // ID de aplicación de la API o del backend
};
